package com.es.marocapp.network


import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.*
import com.es.marocapp.model.responses.translations.TranslationApiResponse
import io.reactivex.Observable
import retrofit2.http.*


interface NetworkApi {

     //Request for GetPreLoginDataCall
    @POST(EndPoints.getprelogindata)
    fun getPreLoginData(@Body request : GetPreLoginDataRequest) : Observable<GetPreLoginDataResponse>

    //Request for GetAccountHolderInformationCall
    @POST(EndPoints.getaccountholderinformation)
    fun getAccountHolderInformation(@Body request : GetAccountHolderInformationRequest) : Observable<GetAccountHolderInformationResponse>

    //Request for GetInitialAuthDetialsCall
    @POST(EndPoints.getinitialauthdetails)
    fun getInitialAuthDetials(@Body request : GetInitialAuthDetailsRequest) : Observable<GetInitialAuthDetailsReponse>

    //Request for GetOTPForRegistrationCall
    @POST(EndPoints.getotpforregistration)
    fun getOTPForRegistration(@Body request : GetOtpForRegistrationRequest) : Observable<GetOtpForRegistrationResponse>

    //Request for RegisterUserCall
    @POST(EndPoints.registeruser)
    fun getRegisterUser(@Body request : RegisterUserRequest) : Observable<RegisterUserResponse>

    //Request for ActivateUserCall
    @POST(EndPoints.activate)
    fun getActivateUser(@Body request : ActivateUserRequest) : Observable<ActivateUserResponse>

    //Request for VerifyOtpAndUpdateAliases
    @POST(EndPoints.verifyotpandupdatealias)
    fun getValidateOtpAndUpdateAliases(@Body request : ValidateOtpAndUpdateAliasesRequest) : Observable<ValidateOtpAndUpdateAliasesResponse>

    //Request for GetOtp
    @POST(EndPoints.getotp)
    fun getOTP(@Body request : GetOptRequest) : Observable<GetOptResponse>

    //Request for CreateCredentials
    @POST(EndPoints.createcredentials)
    fun getCreateCredentialCall(@Body request : CreateCredentialRequest) : Observable<CreateCredentialResponse>

    //Request for ForgotPassword
    @POST(EndPoints.forgotpassword)
    fun getForgotPasswordCall(@Body request : ForgotPasswordRequest) : Observable<ForgotPasswordResponse>

    //Request for LoginWithCert
    @POST(EndPoints.loginwithcert)
    fun getLoginWithCertCall(@Body request : LoginWithCertRequest) : Observable<LoginWithCertResponse>

    //Request for GetBalanceInfoAndLimits
    @POST(EndPoints.getbalanceinfoandlimits)
    fun getBalancesInfoAndLimtCall(@Body request : BalanceInfoAndLimtRequest) : Observable<BalanceInfoAndLimitResponse>

    //Request for ChangePassword
    @POST(EndPoints.changepassword)
    fun getChangePasswordCall(@Body request : ChangePasswordRequest) : Observable<ChangePasswordResponse>

    //Request for GetApprovals
    @POST(EndPoints.getapprovals)
    fun getApprovalsCall(@Body request : GetApprovalRequest) : Observable<GetApprovalsResponse>

    //Request for UserApprovals
    @POST(EndPoints.userapproval)
    fun getUserApprovalsCall(@Body request : UserApprovalRequest) : Observable<UserApprovalResponse>

    //Request for AccountHolderAdditionalInformation
    @POST(EndPoints.getaccountholderadditionalinformation)
    fun getAccountHolderAddtionalInfoCall(@Body request : GetAccountHolderInformationRequest) : Observable<AccountHolderAdditionalInformationResponse>

    //Request for transfer
    @POST(EndPoints.transfer)
    fun getTransferCall(@Body request : TransferRequest) : Observable<TransferResponse>

    //Request for transferQoute
    @POST(EndPoints.transfer_quote)
    fun getTransferQouteCall(@Body request : TransferQouteRequest) : Observable<TransferQouteResponse>

    //Request for merchantPayment
    @POST(EndPoints.merchantpayment)
    fun getMerchantPaymentCall(@Body request : MerchantPaymentRequest) : Observable<MerchantPaymentResponse>

    //Request for merchantQoute
    @POST(EndPoints.merchantpayment_quote)
    fun getMerchantQouteCall(@Body request : MerchantPaymentQuoteRequest) : Observable<MerchantPaymentQuoteResponse>

    //Request for Payment
    @POST(EndPoints.payment)
    fun getPaymentCall(@Body request : PaymentRequest) : Observable<PaymentResponse>

    //Request for paymentQoute
    @POST(EndPoints.payment_quote)
    fun getPaymentQouteCall(@Body request : PaymentQuoteRequest) : Observable<PaymentQuoteResponse>

    //Request for paymentQoute
    @POST(EndPoints.payment_quote)
    fun getSimplePaymentQouteCall(@Body request : SimplePaymentQuoteRequest) : Observable<PaymentQuoteResponse>

    //Request for Payment
    @POST(EndPoints.payment)
    fun getSimplePaymentCall(@Body request : SimplePaymentRequest) : Observable<PaymentResponse>

    //Request for gettransactionhistory
    @POST(EndPoints.gettransactionhistory)
    fun getTrasactionHistoryCall(@Body request : TransactionHistoryRequest) : Observable<TransactionHistoryResponse>

    //Request for getspecifictransactionhistory
    @POST(EndPoints.gettransactionhistory)
    fun getSpecificTrasactionHistoryCall(@Body request : TransactionHistorySpecificPaymentRequest) : Observable<TransactionHistoryResponse>

    //Request for getmultipletransactionhistory
    @POST(EndPoints.gettransactionhistory)
    fun getMultipleTrasactionHistoryCall(@Body request : TransactionHistoryMultiplePaymentRequest) : Observable<TransactionHistoryResponse>

    //Request for GetBalance
    @POST(EndPoints.getbalance)
    fun getBalance(@Body request : BalanceInfoAndLimtRequest) : Observable<GetBalanceResponse>


    //Request for FloatTransferQuote
    @POST(EndPoints.floattransfer_quote)
    fun getFloatTransferQuoteCall(@Body request : FloatTransferQuoteRequest) : Observable<FloatTransferQuoteResponse>

    //Request for FloatTransfer
    @POST(EndPoints.floattransfer)
    fun getFloatTransferCall(@Body request : FloatTransferRequest) : Observable<FloatTransferResponse>


    //Request for GetTranslations
    @GET(EndPoints.getTranslations)
    fun getTranslations() : Observable<TranslationApiResponse>

    //Request for InitiateTransferQuote
    @POST(EndPoints.initiatetransfer_quote)
    fun getInitiateTransferQuoteCall(@Body request : InitiateTransferQuoteRequest) : Observable<InitiateTransferQuoteResponse>

    //Request for InitiateTransfer
    @POST(EndPoints.initiatetransfer)
    fun getInitiateTransferCall(@Body request : InitiateTransferRequest) : Observable<InitiateTransferResponse>

    //Request for GenerateOTP
    @POST(EndPoints.generateotp)
    fun getGenerateOtpCall(@Body request : GenerateOtpRequest) : Observable<GenerateOtpResponse>

    //Request for CashInWithOtpQuote
    @POST(EndPoints.cashinwithotp_quote)
    fun getCashInWithOtpQuoteCall(@Body request : CashInWithOtpQuoteRequest) : Observable<CashInWithOtpQuoteResponse>

    //Request for CashInWithOtp
    @POST(EndPoints.cashinwithotp)
    fun getCashInWithOtpCall(@Body request : CashInWithOtpRequest) : Observable<CashInWithOtpResponse>

    //Request for LogOutUser
    @POST(EndPoints.cashinwithotp)
    fun getLogOutUserCall(@Body request : CashInWithOtpRequest) : Observable<CashInWithOtpResponse>

    //Request for AirTimeUseCases
    @POST(EndPoints.getairtimeusecases)
    fun getAirTimeUseCasesCall(@Body request : GetAirTimeUseCasesRequest) : Observable<GetAirTimeUseCasesResponse>

    //Request for FinancialResourceInfoOne
    @POST(EndPoints.getfinancialresourceinformation_step1)
    fun FinancialResourceInfoOneCall(@Body request : GetFinancialResourceInfoOneRequest) : Observable<GetFinancialResourceInfoOneResponse>

    //Request for FinancialResourceInfoTwo
    @POST(EndPoints.getfinancialresourceinformation_step2)
    fun FinancialResourceInfoTwoCall(@Body request : GetFinancialResourceInfoTwoRequest) : Observable<GetFinancialResourceInfoTwoResponse>

    //Request for AirTimeQuote
    @POST(EndPoints.airtime_quote)
    fun getAirTimeQuoteCall(@Body request : AirTimeQuoteRequest) : Observable<AirTimeQouteResponse>

    //Request for AirTime
    @POST(EndPoints.airtime)
    fun getAirTimeCall(@Body request : AirTimeRequest) : Observable<AirTimeResponse>

    //Request for GetUserSimpleProfile
    @POST(EndPoints.getusertypeprofiles)
    fun getUserSimpleProfile(@Body request : GetUserSimpleProfileRequest) : Observable<GetUserSimpleProfileResponse>

}