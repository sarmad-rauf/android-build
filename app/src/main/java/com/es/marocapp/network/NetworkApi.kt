package com.es.marocapp.network


import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.*
import com.es.marocapp.usecase.approvals.model.response.ResponseApprovals
import io.reactivex.Observable
import retrofit2.http.*


interface NetworkApi {

    // Request for Approvals Data
    @GET(EndPoints.approvals)
    fun getApprovals(): Observable<ResponseApprovals>

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

}