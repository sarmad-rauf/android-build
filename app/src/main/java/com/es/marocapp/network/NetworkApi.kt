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


}